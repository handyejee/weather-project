package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// junit을 받아와야 한다
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class WeatherApplicationTests {

    @Test
    void equalTest() {
        //given
        assertEquals(1, 1);
        //when
        //then
    }

	@Test
	void nullTest(){
		assertNull(null);
	}

	@Test
	void trueTest(){
		assertTrue(1 == 1);
	}


}
