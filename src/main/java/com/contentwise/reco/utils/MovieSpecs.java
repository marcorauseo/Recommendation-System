package com.contentwise.reco.utils;

import com.contentwise.reco.model.Movie;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.List;

public final class MovieSpecs {

    public static Specification<Movie> search(String title,
                                              List<String> genres,
                                              List<String> words) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (title != null && !title.isBlank())
                p = cb.and(p, cb.equal(cb.lower(root.get("title")),
                        title.toLowerCase()));

            if (genres != null && !genres.isEmpty())
                p = cb.and(p, root.join("genres").in(genres));

            if (words != null && !words.isEmpty()) {
                Predicate anyWord = cb.disjunction();
                for (String w : words)
                    anyWord = cb.or(anyWord,
                            cb.like(cb.lower(root.get("title")),
                                    "%" + w.toLowerCase() + "%"));
                p = cb.and(p, anyWord);
            }
            return p;
        };
    }

    private MovieSpecs() { }
}

