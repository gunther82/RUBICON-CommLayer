package ucd.rubicon.network.tinyos;

import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageConverter;

public abstract class RubiconMessageConverterTinyOS<NT extends net.tinyos.message.Message, RT extends RubiconMessage> implements RubiconMessageConverter<NT, RT> {

}
