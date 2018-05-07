package by.kolbun.andersen.obj;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "number"})
public class CachableObject implements Serializable {

    private String name;
    private int number;


}
