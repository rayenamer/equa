package com.rayen.userManaement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "assistant_users")
@DiscriminatorValue("ASSISTANT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AssistantUser extends User {
}
