package com.trelloiii.fotogram.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public @interface IgnoreNull {
}
