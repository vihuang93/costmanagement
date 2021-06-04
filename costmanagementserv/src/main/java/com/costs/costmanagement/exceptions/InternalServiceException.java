package com.costs.costmanagement.exceptions;

import com.costs.costmanagement.apimodels.ErrorDetail;

import java.util.Arrays;
import java.util.List;

public class InternalServiceException extends RuntimeException{
    private List<ErrorDetail> details;

    public InternalServiceException(final ErrorDetail detail) {
        this.details = Arrays.asList(detail);
    }
}
