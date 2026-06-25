package com.maven.rms.exceptionhandler;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.maven.rms.utils.ErrorCode;
import com.maven.rms.utils.ModelEnum;

import org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ErrorMessage {

    private final MessageSource messageSource;

    // public String getMessageExternalMessage(ErrorCode errorCode) {
    // String code = errorCode.toString();
    // String msg = messageSource.getMessage(code, null, Locale.getDefault());
    // if (!StringUtils.equals(code, msg)) {
    // return msg + " (" + errorCode.getErrorNumber() + ")";
    // }

    // // Return default message
    // log.warn("getMessageExternalMessage: errorCode=[{}], not found in
    // messages.properties", errorCode);
    // return messageSource.getMessage(errorCode.getMessage(), null,
    // Locale.getDefault()) + " (" + errorCode.getErrorNumber() + ")";
    // // return messageSource.getMessage("COM_GENERAL_ERROR".toString(), null,
    // Locale.getDefault()) + " (" + "COM_GENERAL_ERROR".toString() + ")";
    // }

    public String getMessageExternalMessage(ErrorCode errorCode, ModelEnum modelEnum) {
        String code = errorCode.toString();
        String msg = messageSource.getMessage(code, null, Locale.getDefault());
        if (!StringUtils.equals(code, msg)) {
            return msg + " (" + modelEnum.getValue() + ")";
        }

        // Return default message
        log.warn("getMessageExternalMessage: errorCode=[{}], not found in messages.properties", errorCode);
        return messageSource.getMessage(errorCode.getMessage(), null, Locale.getDefault()) + " (" + modelEnum.getValue()
                + ")";
        // return messageSource.getMessage("COM_GENERAL_ERROR".toString(), null,
        // Locale.getDefault()) + " (" + "COM_GENERAL_ERROR".toString() + ")";
    }
}
