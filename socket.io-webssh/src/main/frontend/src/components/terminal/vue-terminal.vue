<template>
    <div class="box" style="width: 100%;height: 100%">
        <div id="header"></div>
        <div id="terminal-container-parent" style="width: 100%;height:calc(100% - 21px)">
            <div id="terminal-container" class="terminal"></div>
        </div>
        <div id="bottomdiv">
            <div class="dropup" id="menu">
                <a-dropdown>
                    <a class="ant-dropdown-link" href="#">
                        <a-icon type="setting"/>
                        Menu
                    </a>
                    <!--<a-menu slot="overlay">-->
                        <!--<a-menu-item>-->
                            <!--<a-icon type="user"/>-->
                            <!--Start Log-->
                        <!--</a-menu-item>-->
                        <!--<a-menu-item>-->
                            <!--<a-icon type="user"/>-->
                            <!--Download Log-->
                        <!--</a-menu-item>-->
                        <!--<a-menu-item>-->
                            <!--<a href="javascript:;">3rd menu item</a>-->
                        <!--</a-menu-item>-->
                    <!--</a-menu>-->
                </a-dropdown>
            </div>
            <div id="footer"></div>
            <div id="status"></div>
        </div>
    </div>
</template>

<script>
    /* eslint-disable */
    import * as io from 'socket.io-client'
    // import {Zmodem} from 'zmodem.js'
    import "xterm/dist/xterm.css";
    import * as fit from 'xterm/lib/addons/fit/fit'
    // import * as zmodem from 'xterm/lib/addons/zmodem/zmodem'
    import {Terminal} from 'xterm'
    import "./VueTerminal.css"
    import ZmodemFile from './zmodem-file'

    Terminal.applyAddon(fit)
    // Terminal.applyAddon(zmodem)
    export default {
        name: "VueTerminal",
        props: {
            query: {}
        },
        data() {
            return {
                term: null,
                errorExists: false, upfile: {modal: false, file: null},
                dom: {
                    terminalContainerParent: null,
                    terminalContainer: null,
                    status: null,
                    header: null,
                    dropupContent: null,
                    footer: null
                }, zmodemFile: null,
                rttyFile: null
            }
        }, methods: {

            initTerm() {
                var term = new Terminal()
                this.term = term
                this.dom.terminalContainerParent = document.getElementById('terminal-container-parent')
                this.dom.terminalContainer = document.getElementById('terminal-container')
                this.dom.status = document.getElementById('status')
                this.dom.header = document.getElementById('header')
                this.dom.dropupContent = document.getElementById('dropupContent')
                this.dom.footer = document.getElementById('footer')
                term.open(this.dom.terminalContainer)
                term.focus()
                term.fit();
            }, connectTerm() {
                let that =this
                this.errorExists = false;
                var term = this.term
                var socket = io('/console', {
                    path: '/socket.io',
                    query: this.query
                });
                this.socket = socket
                // this.zmodemFile = new ZmodemFile(socket, term, {
                //     on_detect: (t) => {
                //         if (t == 'r')
                //             this.upfile.modal = true;
                //         else if (t == 's')
                //             ;
                //     }
                // })
                // let zsentry = new Zmodem.Sentry( {
                //     to_terminal(octets) {   },  //i.e. send to the terminal
                //
                //     sender(octets) {  },  //i.e. send to the ZMODEM peer
                //
                //     on_detect(detection) {
                //         //Do this if we determine that what looked like a ZMODEM session
                //         //is actually not meant to be ZMODEM.
                //         if (no_good) {
                //             detection.deny();
                //             return;
                //         }
                //     },  //for when Sentry detects a new ZMODEM
                //
                //     on_retract() {  },  //for when Sentry retracts a Detection
                // } );

//We have to configure whatever gives us new input to send that
//input to zsentry.
//
//EXAMPLE: From web browsers that use WebSocket …
//
//                 ws.addEventListener("message", function(evt) {
//                     zsentry.consume(evt.data);
//                 } );
                term.on('data', function (data) {
                    if(socket.disconnected){
                        socket.connect()
                    }
                    socket.emit('data', data)
                })
                socket.on('connect', function () {
                    socket.emit('geometry', term.cols, term.rows)
                })
                socket.on('data', function (data) {
                    // zsentry.consume(evt.data);
                    term.write(data)

                })
                socket.on('setTerminalOpts', function (data) {
                    term.setOption('cursorBlink', data.cursorBlink)
                    term.setOption('scrollback', data.scrollback)
                    term.setOption('tabStopWidth', data.tabStopWidth)
                    term.setOption('bellStyle', data.bellStyle)
                })

                socket.on('title',this.onTitle)
                socket.on('status', this.onStatus)
                socket.on('ssherror', this.onSSHError)
                socket.on('headerBackground', this.onHeaderBackground)
                socket.on('header', this.onHeader)
                socket.on('footer', this.onFooter)
                socket.on('statusBackground', this.onStatusBackground)
                socket.on('disconnect', this.onDisconnect)
                socket.on('error', this.onError)
                socket.on('menu', this.onMenu)
                socket.on('401', this.authenticationFailed)
                window.addEventListener('resize', this.resize, false)
            },
            onSSHError(data) {
                status.innerHTML = data
                status.style.backgroundColor = 'red'
                this.errorExists = true
            },
            onError(err) {
                if (!this.errorExists) {
                    this.dom.status.style.backgroundColor = 'red'
                    this.dom.status.innerHTML = 'ERROR: ' + err
                }
            }, onDisconnect(err) {
                if (!this.errorExists) {
                    this.dom.status.style.backgroundColor = 'red'
                    this.dom.status.innerHTML =
                        'WEBSOCKET SERVER DISCONNECTED: ' + err
                }
                this.socket.io.reconnection(false)
            },
            onTitle(data){
                window.document.title = data
            },
            onStatusBackground(data) {
                this.dom.status.style.backgroundColor = data
            }, onFooter(data) {
                this.dom.footer.innerHTML = data
            }, onHeader(data) {
                if (data) {
                    this.dom.header.innerHTML = data
                    this.dom.header.style.display = 'block'
                    // header is 19px and footer is 19px, recaculate new terminal-container and resize
                    this.dom.terminalContainerParent.style.height = 'calc(100% - 42px)'
                    this.resize()
                }
            }, onHeaderBackground(data) {
                this.dom.header.style.backgroundColor = data
            }, onStatus(data) {
                this.dom.status.innerHTML = data
            }, onMenu(data) {
                this.drawMenu(data)
            }, drawMenu(data) {
                //this.dom.dropupContent.innerHTML = data
                // logBtn.addEventListener('click', toggleLog)
                // allowreauth && reauthBtn.addEventListener('click', reauthSession)
                // allowreplay && credentialsBtn.addEventListener('click', replayCredentials)
                // loggedData && downloadLogBtn.addEventListener('click', downloadLog)
            },
            authenticationFailed() {
            }, resize() {
                if (this.term) {
                    this.term.fit()
                }
                if (this.socket) {
                    this.socket.emit('resize', {cols: this.term.cols, rows: this.term.rows})
                }
            }
        }, mounted() {
            this.$nextTick(() => {
                this.initTerm()
                this.connectTerm()
            })
        },
        beforeDestroy() {
            if (this.socket) {
                this.socket.disconnect()
            }
            window.removeEventListener('resize', this.resize, false)
        }
    }
</script>

<style scoped>

</style>