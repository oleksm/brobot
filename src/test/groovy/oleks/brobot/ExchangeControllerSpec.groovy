package oleks.brobot

import oleks.brobot.exchange.Exchange
import oleks.brobot.exchange.ExchangeController
import spock.lang.*

@TestFor(ExchangeController)
@Mock(Exchange)
class ExchangeControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
        assert false, "TODO: Provide a populateValidParams() implementation for this generated test suite"
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.exchangeList
            model.exchangeCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.exchange!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def exchange = new Exchange()
            exchange.validate()
            controller.save(exchange)

        then:"The create view is rendered again with the correct model"
            model.exchange!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            exchange = new Exchange(params)

            controller.save(exchange)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/exchange/show/1'
            controller.flash.message != null
            Exchange.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def exchange = new Exchange(params)
            controller.show(exchange)

        then:"A model is populated containing the domain instance"
            model.exchange == exchange
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def exchange = new Exchange(params)
            controller.edit(exchange)

        then:"A model is populated containing the domain instance"
            model.exchange == exchange
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/exchange/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def exchange = new Exchange()
            exchange.validate()
            controller.update(exchange)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.exchange == exchange

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            exchange = new Exchange(params).save(flush: true)
            controller.update(exchange)

        then:"A redirect is issued to the show action"
            exchange != null
            response.redirectedUrl == "/exchange/show/$exchange.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/exchange/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def exchange = new Exchange(params).save(flush: true)

        then:"It exists"
            Exchange.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(exchange)

        then:"The instance is deleted"
            Exchange.count() == 0
            response.redirectedUrl == '/exchange/index'
            flash.message != null
    }
}
