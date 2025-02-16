package org.vaadin.example.application.views.helloworld;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Hello World")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.GLOBE_SOLID)
public class HelloWorldView extends Composite<HorizontalLayout> {

    public HelloWorldView() {
        TextField textField = new TextField("Your name");
        Button buttonPrimary = new Button("Say hello");

        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout layout = new HorizontalLayout(textField, buttonPrimary);
        layout.setPadding(true);
        layout.setMargin(true);
        layout.getStyle().set("padding-left", "10px");
        layout.setAlignItems(Alignment.BASELINE);

        layout.setSpacing(true);

        getContent().add(layout);
    }
}
