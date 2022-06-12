package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다")
    @NotBlank(message = "정확한 이름을 입력해주세요")
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
