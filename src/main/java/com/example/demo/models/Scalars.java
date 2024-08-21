package com.example.demo.models;

import graphql.schema.GraphQLScalarType;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingSerializeException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingParseLiteralException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Scalars {

    public static final GraphQLScalarType DateTime = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime scalar")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } else {
                        throw new CoercingSerializeException("Invalid value '" + dataFetcherResult + "' for DateTime");
                    }
                }

                @Override
                public LocalDateTime parseValue(Object input) {
                    try {
                        return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseValueException("Invalid value '" + input + "' for DateTime");
                    }
                }

                @Override
                public LocalDateTime parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseLiteralException("Invalid value '" + input + "' for DateTime");
                        }
                    } else {
                        throw new CoercingParseLiteralException("Invalid value '" + input + "' for DateTime");
                    }
                }
            })
            .build();

    public static final GraphQLScalarType Date = GraphQLScalarType.newScalar()
            .name("Date")
            .description("Date scalar")
            .coercing(new Coercing<LocalDate, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof LocalDate) {
                        return ((LocalDate) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    } else {
                        throw new CoercingSerializeException("Invalid value '" + dataFetcherResult + "' for Date");
                    }
                }

                @Override
                public LocalDate parseValue(Object input) {
                    try {
                        return LocalDate.parse(input.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseValueException("Invalid value '" + input + "' for Date");
                    }
                }

                @Override
                public LocalDate parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        try {
                            return LocalDate.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseLiteralException("Invalid value '" + input + "' for Date");
                        }
                    } else {
                        throw new CoercingParseLiteralException("Invalid value '" + input + "' for Date");
                    }
                }
            })
            .build();

}
