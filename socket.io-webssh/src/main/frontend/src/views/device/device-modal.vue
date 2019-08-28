<template>
    <a-modal
            :title="title"
            v-model="visible"
            @ok="handleOk"
            @cancel="handleCancel"
            :confirmLoading="confirmLoading"

    >
        <a-form
                :form="form"
                @submit="handleOk">
            <a-form-item style="display: none">
                <a-input v-decorator="['id',{}]"/>
            </a-form-item>
            <a-form-item
                    label="Device Name"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 12 }">
                <a-input
                        v-decorator="['name',{rules: [{ required: true, message: 'Please input your device name!' }]}]"/>
            </a-form-item>
            <!--<a-form-item-->
            <!--label="Connection Mode"-->
            <!--:label-col="{ span: 8 }"-->
            <!--:wrapper-col="{ span: 12 }">-->
            <!--<a-select-->
            <!--v-decorator="['type',{rules: [{ required: true, message: 'Please select connection mode!' }]}]"-->
            <!--placeholder="Select a option"-->
            <!--@change="handleConnectTypeChange"-->
            <!--&gt;-->
            <!--<a-select-option value="direct">-->
            <!--Direct connection-->
            <!--</a-select-option>-->
            <!--<a-select-option value="callhome">-->
            <!--CallHome connection-->
            <!--</a-select-option>-->
            <!--</a-select>-->
            <!--</a-form-item>-->
            <a-form-item
                    label="IP"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 12 }">
                <a-input
                        v-decorator="['ip',{rules: [{ required: true, message: 'Please input your device ip!' }]}]">

                </a-input>
            </a-form-item>
            <a-form-item
                    label="Port"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 12 }">
                <a-input-number
                        v-decorator="['port',{rules: [{ required: true, message: 'Please input your device port!' }]}]"/>
            </a-form-item>
            <a-form-item
                    label="Username"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 12 }">
                <a-input
                        v-decorator="['username',{rules: [{ required: true, message: 'Please input your device ip!' }]}]"/>
            </a-form-item>
            <a-form-item
                    label="Password"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 12 }">
                <a-input type="password"
                        v-decorator="['password',{rules: [{ required: passwordValidate, message: 'Please input your device ip!' }]}]"/>
            </a-form-item>
            <a-form-item
                    label="Descrition"
                    :label-col="{ span: 8 }"
                    :wrapper-col="{ span: 12 }">
                <a-textarea v-decorator="['desc']"/>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script>
    /* eslint-disable */
    import AFormItem from "ant-design-vue/es/form/FormItem";
    import {addDevice, editDevice} from "@api/api";
    import NodeRSA from 'node-rsa';

    const publicKey = '-----BEGIN PUBLIC KEY-----\n' +
        'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIuvXCEiqWwv0bWlc+84IQQ5FokRdCfA\n' +
        'SG6+U8pzU7ymBmEyZkH7clbQx7ooowsVtZkU+e8ntV3NKPkQNzR/7XcCAwEAAQ==\n' +
        '-----END PUBLIC KEY-----'
    // const privateKey = '-----BEGIN PRIVATE KEY-----\n' +
    //     'MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAi69cISKpbC/RtaVz\n' +
    //     '7zghBDkWiRF0J8BIbr5TynNTvKYGYTJmQftyVtDHuiijCxW1mRT57ye1Xc0o+RA3\n' +
    //     'NH/tdwIDAQABAkBwHhnpssOgRaWikiHDIh9GH3haHntIKMs6sGPa/MR6huOQ10yn\n' +
    //     '1ZBMuLnnKQZ+/F3TdsVsfHnOLE4XmWtNPXuRAiEA8cxaTJmkGFtOUojQ4h64qQfW\n' +
    //     'ytXjZOO7NkcFND94n6kCIQCT46U9y+9NUWzYy0PrOV54l9HX5F8ugma5QUVLgX7Y\n' +
    //     'HwIhAO9nXaCqHobfIFUnkWnQ8RSq5kg+Lb8em6XJ+6UXkt7BAiAZegwLW0h8T1yq\n' +
    //     '9EldCmMF71GNGaB0CK7dlyry2PobDQIhAIX3IeL1Ca3Nz+Qe4rNxcKqx/9Y2K2qQ\n' +
    //     'h+VeDYzt1N4C\n' +
    //     '-----END PRIVATE KEY-----'
    export default {
        name: "device-modal",
        components: {AFormItem},
        data() {
            return {
                title: '',
                visible: false, confirmLoading: false, formLayout: 'horizontal',
                edit: false,
                form: this.$form.createForm(this),
                passwordValidate: false
            }
        },
        methods: {
            showModal() {
                this.passwordValidate = false
                this.title = 'Add Device'
                this.edit = false;
                this.visible = true
                this.$nextTick(() => {
                    this.form.setFieldsValue({})
                })
            },
            editModal(record) {

                this.passwordValidate = false
                this.title = 'Edit Device'
                this.edit = true;
                this.visible = true
                this.$nextTick(() => {
                    this.form.setFieldsValue({
                        id: record.id,
                        name: record.name,
                        ip: record.ip,
                        port: record.port,
                        desc: record.desc,
                        username: record.username
                    });
                    // this.form.validateFields(['password'], {force: true});
                });
            },
            handleOk(e) {
                e.preventDefault();
                this.form.validateFields((err, values) => {
                    if (!err) {
                        this.confirmLoading = true;
                        const obj = Object.assign({}, values);
                        const key = new NodeRSA();
                        key.importKey(publicKey, 'pkcs8-public');
                        if (obj.password) {
                            const pass = key.encrypt(obj.password, 'base64', 'utf-8');
                            obj.password = pass
                        }
                        obj.type = 'direct'
                        if (this.edit) {
                            editDevice(obj).then(data => {
                                this.confirmLoading = false;
                                if (data.success) {
                                    this.form.resetFields()
                                    this.visible = false;
                                    this.$emit('on-result', true, obj.id)
                                }
                            }).catch(err=>{
                                this.confirmLoading = false;

                            })
                        } else {
                            addDevice(obj).then(data => {
                                this.confirmLoading = false;
                                if (data.success) {
                                    this.form.resetFields()
                                    this.visible = false;
                                    this.$emit('on-result', true, data.content.id)
                                }
                            }).catch(err=>{
                                this.confirmLoading = false;
                            })
                        }

                    }
                });
            },
            handleCancel(e) {
                this.visible = false
                this.form.resetFields()
            },
            handleConnectTypeChange(value) {

            }
        }
    }
</script>

<style scoped>

</style>