package com.sman20.berlinclock;

import com.sman20.berlinclock.domain.BerlinClockConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfiguration {

  @Bean
  BerlinClockConverter berlinClockConverter() {
    return new BerlinClockConverter();
  }
}
