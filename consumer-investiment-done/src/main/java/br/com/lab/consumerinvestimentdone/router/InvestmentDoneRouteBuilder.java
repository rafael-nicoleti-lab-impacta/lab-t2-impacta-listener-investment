package br.com.lab.consumerinvestimentdone.router;


import br.com.lab.consumerinvestimentdone.dto.DebitAccountRequest;
import br.com.lab.consumerinvestimentdone.dto.InvestmentReceivedEvent;
import com.rabbitmq.client.AMQP;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InvestmentDoneRouteBuilder extends RouteBuilder {

    @Value("${lab.investment-done.paths.client-account-base-url}")
    private String baseUrlAccount;

    @Override
    public void configure() throws Exception {

        errorHandler(deadLetterChannel("direct://deadLetter")
                .maximumRedeliveries(2)
                .redeliveryDelay(2000)
                .backOffMultiplier(2)
                .retryAttemptedLogLevel(LoggingLevel.ERROR)
                .useOriginalMessage());

        from("rabbitmq://amq.direct?queue=eventQueueInvestmentDone&autoDelete=false")
                .unmarshal().json(JsonLibrary.Jackson, InvestmentReceivedEvent.class)
                .process(exchange -> {
                    InvestmentReceivedEvent event = (InvestmentReceivedEvent)exchange.getIn().getBody();

                    exchange.setProperty("accountId", event.getAccountId());

                    exchange.getIn().setBody(new DebitAccountRequest(event.getValueOfInvestment()));
                })
                .log("${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .marshal().json()
                .setHeader(Exchange.HTTP_PATH, simple("api/v1/accounts/${exchangeProperty.accountId}/debit"))
                .to(baseUrlAccount + "/?bridgeEndpoint=true")
                .log("Send debit of Account it's Success!")
                .end();

        from("direct://deadLetter")
                .removeHeaders("rabbitmq*")
                .log("Send message to dead letter!")
                .to("log:errorInRoute?level=ERROR&showProperties=true")
                .to("rabbitmq://amq.direct?queue=errorEventQueueInvestmentDoneDeadLetter&autoDelete=false")
                .end();
    }
}
