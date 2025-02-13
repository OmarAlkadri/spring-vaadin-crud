package org.vaadin.example.views.about;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("About")
@Route("about")
@Menu(order = 2, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class AboutView extends Composite<VerticalLayout> {

    public AboutView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
