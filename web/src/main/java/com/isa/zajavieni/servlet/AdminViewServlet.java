package com.isa.zajavieni.servlet;

import com.isa.zajavieni.dto.UserDto;
import com.isa.zajavieni.provider.TemplateProvider;
import com.isa.zajavieni.service.UserService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin-view")
public class AdminViewServlet extends HttpServlet {

  private Logger logger = LoggerFactory.getLogger(getClass().getName());

  @EJB
  UserService userService;

  @Inject
  TemplateProvider templateProvider;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String email = req.getSession().getAttribute("email").toString();

    if (!userService.isAdmin(email) && !userService.isSuperAdmin(email)) {
      resp.setStatus(404);
      return;
    } else {

      List<UserDto> users = userService.getUsers();

      Template template = templateProvider.getTemplate(getServletContext(), "admin-view.ftlh");
      Map<String, Object> model = new HashMap<>();
      model.put("users", users);

      try {
        template.process(model, resp.getWriter());
      } catch (TemplateException e) {
        logger.error(e.getMessage());
      }

    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
  }
}