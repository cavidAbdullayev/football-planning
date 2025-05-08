//package org.example.footballplanning.exception;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AccessLevel;
//import lombok.SneakyThrows;
//import lombok.experimental.FieldDefaults;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//@Component
//@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
//public class ServletExceptionHandler extends OncePerRequestFilter {
//
//    HandlerExceptionResolver resolver;
//
//    public ServletExceptionHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver){
//        this.resolver = resolver;
//    }
//
//    @Override
//    @SneakyThrows
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
//        try {
//            filterChain.doFilter(request,response);
//        }catch (Exception exception){
//            resolver.resolveException(request,response,null,exception);
//        }
//    }
//
//}
