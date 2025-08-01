package org.example;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ComponentFinderTest {

    @Test
    public void testFindComponentByNameFindsCorrectComponent() {
        JPanel panel = new JPanel();
        JButton button = new JButton("Click Me");
        button.setName("myButton");
        panel.add(button);

        JButton result = ComponentFinder.findComponentByNameAsType(panel, "myButton", JButton.class);
        assertThat(result).isSameAs(button);
    }

    @Test
    public void testFindComponentByNameReturnsEmptyIfNotFound() {
        JPanel panel = new JPanel();
        assertThat(ComponentFinder.findComponentByName(panel, "nonexistent")).isEmpty();
    }

    @Test
    public void testFindComponentByNameAsTypeThrowsIfTypeMismatch() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Label");
        label.setName("myLabel");
        panel.add(label);

        // This should throw an assertion error because the type is wrong
        assertThatThrownBy(() -> {
            ComponentFinder.findComponentByNameAsType(panel, "myLabel", JButton.class);
        }).isInstanceOf(AssertionError.class)
                .hasMessageContaining("should be of type JButton");
    }

    @Test
    public void testFindsComponentInNestedContainers() {
        JPanel outer = new JPanel();
        JPanel inner = new JPanel();
        JButton button = new JButton("Nested");
        button.setName("nestedButton");
        inner.add(button);
        outer.add(inner);

        JButton result = ComponentFinder.findComponentByNameAsType(outer, "nestedButton", JButton.class);
        assertThat(result).isSameAs(button);
    }
}
