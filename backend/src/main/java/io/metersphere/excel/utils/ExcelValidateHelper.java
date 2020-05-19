package io.metersphere.excel.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Set;

@Component
public class ExcelValidateHelper {

    private static ExcelValidateHelper excelValidateHelper;

    @Resource
    LocalValidatorFactoryBean localValidatorFactoryBean;

    public static  <T> String validateEntity(T obj) throws NoSuchFieldException {
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<T>> set = excelValidateHelper.localValidatorFactoryBean.getValidator().validate(obj, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> cv : set) {
                Field declaredField = obj.getClass().getDeclaredField(cv.getPropertyPath().toString());
                ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                //拼接错误信息，包含当前出错数据的标题名字+错误信息
                result.append(annotation.value()[0]+cv.getMessage()).append("; ");
            }
        }
        return result.toString();
    }

    @PostConstruct
    public void initialize() {
        excelValidateHelper = this;
        excelValidateHelper.localValidatorFactoryBean = this.localValidatorFactoryBean;
    }
}