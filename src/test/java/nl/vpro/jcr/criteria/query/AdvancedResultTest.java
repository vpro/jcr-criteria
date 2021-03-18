package nl.vpro.jcr.criteria.query;

import java.util.NoSuchElementException;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *
 * @author Michiel Meeuwissen
 */
public class AdvancedResultTest  {

    @Test
    public void empty() {
        AdvancedResult emptyResult = AdvancedResult.EMPTY_RESULT;

        assertThat(emptyResult.getPage()).isEqualTo(0);
        assertThat(emptyResult.getNumberOfPages()).isEqualTo(0);
        assertThat(emptyResult.getFirstResult()).isNull();
        assertThat(emptyResult.getItemsPerPage()).isNull();
        assertThat(emptyResult.getTotalSize()).isEqualTo(0);
        assertThat(emptyResult.getSpellCheckerSuggestion()).isNull();

        assertThat(emptyResult.getItems().hasNext()).isFalse();
        assertThat(emptyResult.iterator().hasNext()).isFalse();
        assertThat(emptyResult.stream()).isEmpty();
        assertThat(emptyResult.getItems().getSize()).isEqualTo(0);
        assertThat(emptyResult.getItems().getPosition()).isEqualTo(0);
        assertThatThrownBy(() -> emptyResult.getItems().next()).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> emptyResult.getItems().remove()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> emptyResult.getItems().skip(1)).isInstanceOf(NoSuchElementException.class);
    }


}
