package cat.corredors.backoffice.users.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cat.corredors.backoffice.users.crosscutting.ErrorBean;

public class ResponseError implements Serializable {
    private static final long serialVersionUID = 0x3252e46fe398ef1cL;
    
    private List<ErrorBean> errors;
    
    public ResponseError() {
        errors = new ArrayList<ErrorBean>();
    }

    public ResponseError(List<ErrorBean> errors) {
        this.errors = errors;
    }

    public ResponseError(int code, String message) {
    	errors = new ArrayList<ErrorBean>();    
        errors.add(new ErrorBean(code, message));
    }

    public List<ErrorBean> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorBean> errors) {
        this.errors = errors;
    }
}
