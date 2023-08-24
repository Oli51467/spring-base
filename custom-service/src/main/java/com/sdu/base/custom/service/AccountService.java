package com.sdu.base.custom.service;

import com.sdu.base.common.response.ResponseResult;
import com.sdu.base.custom.entity.dto.RegisterReq;

public interface AccountService {

    ResponseResult register(RegisterReq req);
}
