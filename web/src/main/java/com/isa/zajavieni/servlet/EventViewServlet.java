package com.isa.zajavieni.servlet;

import com.isa.zajavieni.dto.EventDto;
import com.isa.zajavieni.entity.Event;
import com.isa.zajavieni.provider.TemplateProvider;
import com.isa.zajavieni.service.EventDtoService;
import com.isa.zajavieni.service.FavouriteEventService;
import com.isa.zajavieni.service.SendEmailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.Map;

@WebServlet("/event-view")
public class EventViewServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    @EJB
    private EventDtoService eventDtoService;

    @Inject
    private FavouriteEventService favouriteEventService;

    @Inject
    private TemplateProvider templateProvider;

    @Inject
    private SendEmailService sendEmailService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        Long id = null;
        EventDto eventDto = new EventDto();
        Event event = new Event();
        String eventId = req.getParameter("id");
        if (eventId != null || !eventId.isEmpty() || NumberUtils.isDigits(eventId)) {
            id = Long.valueOf(eventId);
            eventDto = eventDtoService.findById(id);
            event=eventDtoService.findEventById(id);
        }

        Template template = templateProvider.getTemplate(getServletContext(), "event-details.ftlh");
        Map<String, Object> model = new HashMap<>();
        model.put("event", eventDto);


        Long userId = 2L;
        Boolean isFavourite = false;
        logger.info("Favourite: {}", isFavourite);
        if(favouriteEventService.findListOfUserFavouriteEvents(userId).stream().map(e -> e.getId()).collect(
            Collectors.toList()).contains(event.getId())){
            isFavourite = true;
        }
        logger.info("Favourite: {}", isFavourite);
        model.put("isFavourite", isFavourite);

        try {
            template.process(model, resp.getWriter());
        } catch (TemplateException e) {
            logger.error(e.getMessage());
        }

        sendEmailService.sendEmailForAllUsers(id);
    }
}