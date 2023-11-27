package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){   // Spring에서 Model에 데이터를 실어서 뷰에 전달
        model.addAttribute("data", "hello!!"); // name: 'data / value: 'hello!!
        return "hello";  // templates/'html파일명'
    }


}
