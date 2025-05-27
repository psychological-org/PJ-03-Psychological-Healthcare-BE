package com.microservices.attendance.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttendanceNotFoundException extends RuntimeException {

    private final String msg;
}