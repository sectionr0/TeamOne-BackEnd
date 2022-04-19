package com.mjuteam2.TeamOne.member.controller;

import com.mjuteam2.TeamOne.member.dto.MemberResponse;
import com.mjuteam2.TeamOne.util.dto.BooleanResponse;
import com.mjuteam2.TeamOne.member.domain.Member;
import com.mjuteam2.TeamOne.member.config.SessionConst;
import com.mjuteam2.TeamOne.member.service.SignInService;
import com.mjuteam2.TeamOne.util.dto.ApiResponse;
import com.mjuteam2.TeamOne.util.exception.ErrorCode;
import com.mjuteam2.TeamOne.util.exception.ErrorDto;
import com.mjuteam2.TeamOne.member.dto.SignInForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class SignInController {

    private final SignInService signInService;

    /**
     * 로그인
     * @param loginForm     로그인 관련 DTO
     * @param bindingResult 검증 관련
     * @return 성공시 로그인 맴버 객체 JSON으로 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody SignInForm loginForm,
                                   BindingResult bindingResult,
                                   HttpServletRequest request) throws LoginException {
        if (bindingResult.hasErrors()) {
            log.error("SignIn Errors = {}", bindingResult.getFieldErrors());
            return ApiResponse.badRequest(ErrorDto.convertJson(bindingResult.getFieldErrors()));
        }
        MemberResponse memberResponse = signInService.login(loginForm, request);
        log.info("member login = {}", memberResponse);
        return ApiResponse.success(memberResponse);
    }

    /**
     * 로그아웃
     * 세션 삭제해서 로그아웃 진행
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("member logout = {}", request.getAttribute(SessionConst.LOGIN_MEMBER));
        signInService.logout(request);
        return ApiResponse.success(new BooleanResponse(true));
    }

    /**
     *  예외 처리
     */
    // 로그인 관련 예외
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> loginExHandle(LoginException e) {
        log.error("[exceptionHandle] ex", e);
        return ApiResponse.badRequest(new ErrorDto(ErrorCode.LOGIN_ERROR, e.getMessage()));
    }
}