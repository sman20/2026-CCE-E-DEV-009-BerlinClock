package com.sman20.berlinclock.web;

import com.sman20.berlinclock.domain.BerlinClockConverter;
import com.sman20.berlinclock.domain.DigitalTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversionController {
    private final BerlinClockConverter converter;

    public ConversionController(BerlinClockConverter converter) {
        this.converter = converter;
    }

    @GetMapping("/api/berlin-clock-time")
    public BerlinClockTimeResponse convert(@RequestParam String digitalTime) {
        return new BerlinClockTimeResponse(converter.toBerlinClockTime(DigitalTime.parse(digitalTime)).lamps());
    }

    public record BerlinClockTimeResponse(String berlinClockTime) {}
}
