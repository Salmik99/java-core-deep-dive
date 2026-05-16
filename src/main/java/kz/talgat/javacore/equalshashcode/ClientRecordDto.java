package kz.talgat.javacore.equalshashcode;

/**
 * Пример record DTO.
 * <p>
 * Record автоматически генерирует {@code equals()} и {@code hashCode()} по
 * всем компонентам: {@code iin} и {@code fullName}.
 */
public record ClientRecordDto(String iin, String fullName) {
}
