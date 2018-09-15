package com.test

import grails.converters.JSON

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PersonController {

    def sessionFactory

    static defaultAction = 'get'

    PersonService personService

    def get(Long id) {

        render (personService.getPerson(id) as JSON)
    }

    def list(String id) {

        render (personService.getPeople(id ?: '') as JSON)
    }

    def listAll() {
        def start = System.currentTimeMillis()
        def map = [:]
        params.max = 20000
        params.offset = 0
        def lists = Person.createCriteria().list(params,{

        })
        map.total = lists.size()
        map.data = lists
        def end = System.currentTimeMillis()
        def consume = (end - start) / 1000
        map.consume = "耗时：$consume 秒"
        println "耗时：$consume 秒"
        render map as JSON
    }

    def init() {
        log.info("=======初始化数据========")
        (0..200000).eachWithIndex { o,index ->
            Person.withTransaction {
                new Person(username: "zhangsan${index+1}").save()
            }
            if(index.mod(200) == 0) {
                sessionFactory.currentSession.flush()
                sessionFactory.currentSession.clear()
            }
        }
        render "====初始化完成====="
    }


    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Person.list(params), model:[personCount: Person.count()]
    }

    def show(Person person) {
        respond person
    }

    def create() {
        respond new Person(params)
    }

    @Transactional
    def save(Person person) {
        if (person == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (person.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond person.errors, view:'create'
            return
        }

        person.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'person.label', default: 'Person'), person.id])
                redirect person
            }
            '*' { respond person, [status: CREATED] }
        }
    }

    def edit(Person person) {
        respond person
    }

    @Transactional
    def update(Person person) {
        if (person == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (person.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond person.errors, view:'edit'
            return
        }

        person.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'person.label', default: 'Person'), person.id])
                redirect person
            }
            '*'{ respond person, [status: OK] }
        }
    }

    @Transactional
    def delete(Person person) {

        if (person == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        person.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'person.label', default: 'Person'), person.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'person.label', default: 'Person'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
