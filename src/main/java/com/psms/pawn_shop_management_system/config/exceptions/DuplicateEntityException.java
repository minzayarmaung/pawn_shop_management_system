package com.psms.pawn_shop_management_system.config.exceptions;

public class DuplicateEntityException extends RuntimeException{
    public DuplicateEntityException(String message){
        super(message);
    }
}
