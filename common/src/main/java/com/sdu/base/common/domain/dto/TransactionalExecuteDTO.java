package com.sdu.base.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionalExecuteDTO {

    private String className;

    private String methodName;

    private String parameterTypes;

    private Object[] args;
}

