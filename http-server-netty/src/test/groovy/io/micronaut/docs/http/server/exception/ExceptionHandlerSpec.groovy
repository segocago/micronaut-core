/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs.http.server.exception

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class ExceptionHandlerSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            'spec.name': ExceptionHandlerSpec.simpleName
    ], Environment.TEST)

    @AutoCleanup
    @Shared
    HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

    void "test OutOfStockException is handled by ExceptionHandler"() {
        when:
        HttpRequest request = HttpRequest.GET('/books/stock/1234')
        Integer stock = client.toBlocking().retrieve(request, Integer)

        then:
        noExceptionThrown()
        stock != null
        stock == 0
    }

    void "test wrapped HttpStatusException in CompletionException is unwrapped and handled by ExceptionHandler"() {
        when:
        HttpRequest request = HttpRequest.GET('/books/stock/future/1234')
        Integer stock = client.toBlocking().retrieve(request, Integer)

        then:
        noExceptionThrown()
        stock != null
        stock == 1234
    }

    void "test wrapped HttpStatusException in ExecutionException is unwrapped and handled by ExceptionHandler"() {
        when:
        HttpRequest request = HttpRequest.GET('/books/stock/blocking/1234')
        Integer stock = client.toBlocking().retrieve(request, Integer)

        then:
        noExceptionThrown()
        stock != null
        stock == 1234
    }

    void "test error route with @Status"() {
        when:
        HttpRequest request = HttpRequest.GET('/books/null-pointer')
        HttpResponse<String> stock = client.toBlocking().exchange(request, String)

        then:
        noExceptionThrown()
        stock.getBody(String).get() == "NPE"
        stock.status() == HttpStatus.MULTI_STATUS
    }

    void "test exception handler returning a publisher"() {
        when:
        HttpRequest request = HttpRequest.GET('/books/reactive')
        HttpResponse<String> stock = client.toBlocking().exchange(request, String)

        then:
        noExceptionThrown()
        stock.getBody(String).get() == "[reactive handler]"
        stock.status() == HttpStatus.OK
    }
}
