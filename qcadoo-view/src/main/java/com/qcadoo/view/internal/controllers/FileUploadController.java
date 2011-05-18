/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 0.4.1
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.view.internal.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.view.api.crud.CrudService;
import com.qcadoo.view.constants.QcadooViewConstants;
import com.qcadoo.view.internal.components.file.FileUtils;

@Controller
public class FileUploadController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private CrudService crudController;

    @RequestMapping(value = "fileUpload", method = RequestMethod.GET)
    public ModelAndView upload(final Locale locale) {
        ModelAndView mav = getCrudPopupView(QcadooViewConstants.VIEW_FILE_UPLOAD, locale);

        mav.addObject("headerLabel", translationService.translate("qcadooView.fileUpload.header", locale));
        mav.addObject("buttonLabel", translationService.translate("qcadooView.fileUpload.button", locale));
        mav.addObject("chooseFileLabel", translationService.translate("qcadooView.fileUpload.chooseFileLabel", locale));

        return mav;
    }

    private ModelAndView getCrudPopupView(final String viewName, final Locale locale) {
        Map<String, String> crudArgs = new HashMap<String, String>();
        crudArgs.put("popup", "true");
        return crudController.prepareView(QcadooViewConstants.PLUGIN_IDENTIFIER, viewName, crudArgs, locale);
    }

    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") final MultipartFile file, final Locale locale) {
        String error = null;
        String path = null;

        try {
            path = FileUtils.upload(file);
        } catch (IOException e) {
            error = e.getMessage();
        }

        JSONObject response = new JSONObject();
        try {
            if (path != null) {
                response.put("fileLastModificationDate", FileUtils.getLastModificationDate(path));
                response.put("fileUrl", FileUtils.getUrl(path));
                response.put("fileName", FileUtils.getName(path));
                response.put("filePath", path);
            } else {
                response.put("fileLastModificationDate", "");
                response.put("fileUrl", "");
                response.put("fileName", "");
                response.put("filePath", "");
            }
            response.put("fileUploadError", error);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return response.toString();
    }
}