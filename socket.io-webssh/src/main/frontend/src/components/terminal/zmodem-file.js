function ZmodemFile(socket, term, opt) {
    this.state = '';
    this.socket = socket;
    this.term = term;
    this.cache = [];
    this.buffer = [];
    let zsentry = new Zmodem.Sentry( {
        to_terminal(octets) {  },  //i.e. send to the terminal

        sender(octets) {   },  //i.e. send to the ZMODEM peer

        on_detect(detection) {   },  //for when Sentry detects a new ZMODEM

        on_retract() {  },  //for when Sentry retracts a Detection
    } );
    this.to_term = function(octets) {
        this.term.write(Buffer.from(octets).toString());
    }
    this.detect = function (input) {
        let type = '';

        if (input.byteLength < 3)
            return '';

        input = new Uint8Array(input);
    }
    this.consume = function (input) {
        if (this.state == '') {

        } else {

        }
        return false
    }
    this.sendFile = function (file) {
        this.fr = new FileReader();

        this.state = 'sending';
        let offset = 0;
    }
}

export default ZmodemFile