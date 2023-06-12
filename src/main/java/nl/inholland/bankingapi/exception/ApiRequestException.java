package nl.inholland.bankingapi.exception;


import org.springframework.http.HttpStatus;

public class ApiRequestException extends RuntimeException {
    private  HttpStatus status;
    public ApiRequestException(String message, HttpStatus status) {
        super(message);
        this.status=status;
    }

    public HttpStatus getStatus()
    {
        return  this.status;
    }

}
