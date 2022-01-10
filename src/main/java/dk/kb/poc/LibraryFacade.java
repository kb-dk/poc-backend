/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package dk.kb.poc;

import dk.kb.poc.model.v1.BookDto;
import dk.kb.poc.webservice.ExportWriter;
import dk.kb.poc.webservice.exception.NotFoundServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A demonstration class that handles some of the webservice endpoints.
 *
 * The Facade Pattern (https://en.wikipedia.org/wiki/Facade_pattern) is useful
 * here at it makes it easier to update or delete & regenerate the ServiceImpl
 * from the OpenAPI-specification.
 */
public class LibraryFacade {
    private static final Logger log = LoggerFactory.getLogger(LibraryFacade.class);

    // Deleted books are represented with DELETED_BOOK
    private static final Map<String, BookDto> books = new HashMap<>();
    private static final BookDto DELETED_BOOK = new BookDto().id("___deleted___");

    public static synchronized BookDto addBook(BookDto book) {
        books.put(book.getId(), book);
        return book;
    }

    public static String deleteBook(String id) {
        if (books.get(id) == DELETED_BOOK) {
            throw new NotFoundServiceException(
                    "The book with id '" + id + "' was not available and thus could not be deleted");
        }
        books.put(id, DELETED_BOOK);
        return "The book with id '" + id + "' was successfully deleted";
    }


    public static BookDto getBook(String id) {
        BookDto book = books.get(id);
        if (book == DELETED_BOOK) {
            throw new NotFoundServiceException("The book with id '" + id + "' was not available");
        }
        if (book != null) {
            return book;
        }
        book = new BookDto()
                .id(id)
                .title("Random Book #" + new Random().nextInt(Integer.MAX_VALUE));
        books.put(id, book);
        return book;
    }

    public static void deliverBooks(ExportWriter writer, String query, Long max) {
        log.debug("Delivering {} books. query='{}'", max, query);
        AtomicInteger delivered = new AtomicInteger(0);
        books.values().stream()
                .filter(book -> book != DELETED_BOOK)
                .limit(max)
                .peek(ignored -> delivered.incrementAndGet())
                .forEach(writer::write);

        Random random = new Random();
        for (int i = 0 ; i < max - delivered.get() ; i++) {
            String id = Integer.toString(random.nextInt(Integer.MAX_VALUE));
            BookDto book = new BookDto().id(id).title("Random Book #" + id + ", query=" + query);
            writer.write(book);
        }
    }
}
