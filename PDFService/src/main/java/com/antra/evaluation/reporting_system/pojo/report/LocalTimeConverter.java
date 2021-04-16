package com.antra.evaluation.reporting_system.pojo.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class LocalTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

	@Override
	public String convert(LocalDateTime instant) {
		return instant == null ? null : DATE_TIME_FORMATTER.format(instant);
	}

	@Override
	public LocalDateTime unconvert(String str) {
		return str == null ? null : LocalDateTime.from(DATE_TIME_FORMATTER.parse(str));
	}
}
