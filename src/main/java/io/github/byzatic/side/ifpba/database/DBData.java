package io.github.byzatic.side.ifpba.database;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "tomilino_data", schema = "public")
public class DBData {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tomilino_data_id_seq"
    )
    @SequenceGenerator(
            name = "tomilino_data_id_seq",
            sequenceName = "tomilino_data_id_seq",
            allocationSize = 1
    )
    private Integer id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "reg_name")
    private String regName;

    @Column(name = "data")
    private Double data;

    public DBData() {
    }

    public DBData(LocalDateTime timestamp, String regName, Double data) {
        this.timestamp = timestamp;
        this.regName = regName;
        this.data = data;
    }
}