package org.example.footballplanning.helper;

import lombok.SneakyThrows;
import org.example.footballplanning.bean.base.BaseResponseBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Component
public class GeneralHelper {
    public static boolean isNullOrEmpty(String txt){
        return txt==null||txt.trim().isEmpty();
    }
    public static boolean containsNullOrEmptyValue(String... values){
        return Stream.of(values)
                .anyMatch(value->value==null||value.trim().isEmpty());
    }
    public static boolean containsNull(Integer... values){
        return Stream.of(values).anyMatch(Objects::isNull);
    }
    public static <T extends BaseResponseBean> T createResponse(T response, String message) {
        response.setMessage(message);
        return response;
    }
    @SneakyThrows
    public static <T,R> T mapFields(T to, R from){
        Class<?>entityClass= to.getClass();
        Class<?>requestClass= from.getClass();

        Field[] requestFields= requestClass.getDeclaredFields();
        Field[] entityFields= entityClass.getDeclaredFields();
        Set<String> entityFieldNames=new HashSet<>();
        for(Field entityField: entityFields){
            entityFieldNames.add(entityField.getName());
        }
        for(Field requestField: requestFields){
            requestField.setAccessible(true);
            Object value=requestField.get(from);
            if(value==null||value instanceof String&&isNullOrEmpty(value.toString())) {
                continue;
            }
            if(entityFieldNames.contains(requestField.getName())){
            Field entityField=entityClass.getDeclaredField(requestField.getName());
            entityField.setAccessible(true);

            if(entityField.getType().isAssignableFrom(requestField.getType())){
                entityField.set(to,value);
            }
        }
        }
        return to;
    }
    @SneakyThrows
    public static void validateRequest(Object request){

        Field[] fields=request.getClass().getDeclaredFields();

        for(Field field: fields){
            field.setAccessible(true);
            Object value=field.get(request);
            if (value instanceof String && isNullOrEmpty(value.toString()) || value == null) {
                throw new RuntimeException(field.getName() + " cannot be null or empty!");
            }
        }
    }
    @SneakyThrows
    public static <T,R> T updateDifferentFields(T entity, R request){
        Class<?>entityClass=entity.getClass();
        Class<?>requestClass=request.getClass();

        Field[] requestFields=requestClass.getFields();

        for (Field requestField : requestFields) {
            Field entityField = entityClass.getDeclaredField(requestField.getName());

            requestField.setAccessible(true);
            entityField.setAccessible(true);

            Object requestValue = requestField.get(request);
            Object entityValue = entityField.get(entity);
            if ((requestValue != null && !requestValue.equals(entityValue))) {
                entityField.set(entity, requestValue);
            }

        }
return entity;
    }
    public static LocalDate strToDate(String strDate){
        return LocalDate.parse(strDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    public static String dateToStr(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
