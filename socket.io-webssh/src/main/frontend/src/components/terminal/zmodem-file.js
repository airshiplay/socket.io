function ZmodemFile(socket, term, opt) {
    this.state = '';
    this.socket = socket;
    this.term = term;
    this.cache = [];
    this.buffer = [];
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