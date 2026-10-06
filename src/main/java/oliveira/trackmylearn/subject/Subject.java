package oliveira.trackmylearn.subject;

import jakarta.persistence.*;

@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "weekly_goal_minutes")
    private Integer weeklyGoalMinutes;

    protected Subject () { }

    public Subject(String name, Integer weeklyGoalMinutes) {
        this.name = name;
        this.weeklyGoalMinutes = weeklyGoalMinutes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getWeeklyGoalMinutes() {
        return weeklyGoalMinutes;
    }
}
