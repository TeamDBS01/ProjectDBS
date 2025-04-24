package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a review_delete entity.
 * Mapped to the "review_delete" table in the database.
 *
 * @author Sabarish Iyer
 */

@Data
@Entity
@Table(name = "review_delete")
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDelete {

    @Id
    @Column(name = "delete_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long deleteId;
    @Column(name = "review_id")
    private long reviewId;
    @Column(name = "reason")
    private String reason;

    public ReviewDelete(long reviewId, String reason) {
        this.reviewId = reviewId;
        this.reason = reason;
    }
}
