package com.royalresearch.services.controllers;

import com.royalresearch.services.entities.Task;
import com.royalresearch.services.entities.User;
import com.royalresearch.services.services.TaskService;
import com.royalresearch.services.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String home(){

        return "homepage";
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpSession session){

        session = request.getSession();
        if(session.getAttribute("session") != null){
            return "redirect:/dashboard";
        }

        return "login";
    }

    @RequestMapping(value="/treat_login", method = RequestMethod.POST)
    public String treatlogin(HttpServletRequest request, HttpSession session){

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if(userService.login(username,password)){
            User user = userService.getUserByCredentials(username,password);
            userService.setLastLoginToNow(user);
            session = request.getSession();
            session.setAttribute("session",user);
            return "redirect:/dashboard";
        }else{
            return "redirect:/?error=1";
        }
    }

    @RequestMapping(value="/signup", method = RequestMethod.GET)
    public String signup(HttpServletRequest request, HttpSession session){

        session = request.getSession();

        return "signup";
    }

    @RequestMapping(value="/treat_signup", method = RequestMethod.POST)
    public String treatsignup(HttpServletRequest request) throws NoSuchAlgorithmException {

        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String email = request.getParameter("email");

        String dob_parameter = request.getParameter("dob");
        LocalDate dateOfBirth = LocalDate.parse(dob_parameter);

        String username = request.getParameter("username");

        String password_parameter = request.getParameter("password");

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(password_parameter.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        String password = hexString.toString();
        User user = new User(name,address,email,dateOfBirth,username,password);

        try{
            userService.createUser(user);
        }catch (Exception e){
            return "redirect:/login";
        }

        return "redirect:/login";
    }

    @RequestMapping(value="/dashboard", method = RequestMethod.GET)
    public String dashboard(HttpServletRequest request, HttpSession session, Model model){

        session = request.getSession();
        if(session.getAttribute("session") == null){
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("session");

        LocalDateTime userLastLogin = user.getLastLoginTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lastLogin = userLastLogin.format(formatter);

        if(lastLogin==null){
            model.addAttribute(lastLogin,"It's your first connection.");
        }

        model.addAttribute("lastLogin",lastLogin);

        List<Task> listOfTasks = taskService.getAllTasks();

        if(listOfTasks!=null){
            model.addAttribute("tasks",listOfTasks);
        }

        return "dashboard";
    }

    @RequestMapping(value = "/treat_task", method = RequestMethod.POST)
    public String treattask(@RequestParam("description") String description,
                            @RequestParam("attachment") MultipartFile attachment) throws IOException {
        Task task = new Task();
        task.setDescription(description);

        taskService.createTask(task);

        return "redirect:/dashboard";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpSession session){
        session = request.getSession();
        session.invalidate();
        return "redirect:/login";
    }
}
