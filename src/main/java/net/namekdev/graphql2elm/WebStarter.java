package net.namekdev.graphql2elm;

import org.teavm.jso.browser.*;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.*;

import java.util.Locale;

import static org.antlr.v4.runtime.atn.ATNDeserializer.SERIALIZED_VERSION;


public class WebStarter {

    public static void main(String[] args) {
        HTMLDocument document = Window.current().getDocument();

        HTMLElement preEl = document.createElement("pre");
        preEl.setInnerHTML("hey just a test");
        document.getBody().appendChild(preEl);
    }
}
