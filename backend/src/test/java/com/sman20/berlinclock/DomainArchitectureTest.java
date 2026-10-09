package com.sman20.berlinclock;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class DomainArchitectureTest {
  @Test
  void domainDependsOnlyOnItselfAndJava() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideOutsideOfPackages("..domain..", "java..")
        .check(
            new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.sman20.berlinclock.domain"));
  }
}
