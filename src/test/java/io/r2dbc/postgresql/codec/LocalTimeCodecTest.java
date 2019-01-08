/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.postgresql.codec;

import io.r2dbc.postgresql.client.Parameter;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static io.r2dbc.postgresql.message.Format.FORMAT_BINARY;
import static io.r2dbc.postgresql.message.Format.FORMAT_TEXT;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.MONEY;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.TIME;
import static io.r2dbc.postgresql.type.PostgresqlObjectId.VARCHAR;
import static io.r2dbc.postgresql.util.ByteBufUtils.encode;
import static io.r2dbc.postgresql.util.TestByteBufAllocator.TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

final class LocalTimeCodecTest {

    @Test
    void constructorNoByteBufAllocator() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalTimeCodec(null))
            .withMessage("byteBufAllocator must not be null");
    }

    @Test
    void decode() {
        LocalTime localTime = LocalTime.now();

        assertThat(new LocalTimeCodec(TEST).decode(encode(TEST, localTime.toString()), FORMAT_TEXT, LocalTime.class))
            .isEqualTo(localTime);
    }

    @Test
    void decodeNoByteBuf() {
        assertThat(new LocalTimeCodec(TEST).decode(null, FORMAT_TEXT, LocalTime.class)).isNull();
    }

    @Test
    void doCanDecode() {
        LocalTimeCodec codec = new LocalTimeCodec(TEST);

        assertThat(codec.doCanDecode(FORMAT_BINARY, TIME)).isFalse();
        assertThat(codec.doCanDecode(FORMAT_TEXT, MONEY)).isFalse();
        assertThat(codec.doCanDecode(FORMAT_TEXT, TIME)).isTrue();
    }

    @Test
    void doCanDecodeNoFormat() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalTimeCodec(TEST).doCanDecode(null, VARCHAR))
            .withMessage("format must not be null");
    }

    @Test
    void doCanDecodeNoType() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalTimeCodec(TEST).doCanDecode(FORMAT_TEXT, null))
            .withMessage("type must not be null");
    }

    @Test
    void doEncode() {
        LocalTime localTime = LocalTime.now();

        assertThat(new LocalTimeCodec(TEST).doEncode(localTime))
            .isEqualTo(new Parameter(FORMAT_TEXT, TIME.getObjectId(), encode(TEST, localTime.toString())));
    }

    @Test
    void doEncodeNoValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalTimeCodec(TEST).doEncode(null))
            .withMessage("value must not be null");
    }

    @Test
    void encodeNull() {
        assertThat(new LocalTimeCodec(TEST).encodeNull())
            .isEqualTo(new Parameter(FORMAT_TEXT, TIME.getObjectId(), null));
    }

}
