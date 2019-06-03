<template>
    <a-layout id="components-layout-demo-fixed">
        <a-layout-header :style="{ position: 'fixed', zIndex: 999, width: '100%' }">
            <div class="logo"><span>Web SSH</span></div>
            <a-menu
                    theme="dark"
                    mode="horizontal"
                    :defaultSelectedKeys="['1']"
                    :style="{ lineHeight: '64px' }"
                    :selectedKeys="[currentMenu]"
                    @click="handleMenuClick"
            >
                <a-menu-item key="equipment">equipment management</a-menu-item>
                <a-menu-item key="user">user management</a-menu-item>
                <a-menu-item key="enterprise">enterprise management</a-menu-item>
            </a-menu>
        </a-layout-header>
        <a-layout-content :style="{ padding: '0 50px', marginTop: '64px' }">
            <a-breadcrumb :style="{ margin: '16px 0' }">
                <!--<a-breadcrumb-item>Home</a-breadcrumb-item>-->
                <!--<a-breadcrumb-item>List</a-breadcrumb-item>-->
                <!--<a-breadcrumb-item>App</a-breadcrumb-item>-->
            </a-breadcrumb>
            <div :style="{ background: '#fff', padding: '24px', minHeight: '380px' }">
                <router-view/>
            </div>
        </a-layout-content>
        <a-layout-footer :style="{ textAlign: 'center' }">
            WebSSH ©2019 Created by Airlenet
        </a-layout-footer>
    </a-layout>
</template>

<script>
    export default {
        name: "main1",data(){
            return {
                currentMenu:this.$route.name
            }
        },methods:{
            handleMenuClick(e) {
                this.currentMenu = e.key
                let {name, params, query} = {}
                name= this.currentMenu
                this.$router.push({
                    name,
                    params,
                    query
                })
            }

        }
    }
</script>

<style>
    #components-layout-demo-fixed .logo {
        width: 120px;
        height: 31px;
        background: rgba(255, 255, 255, .2);
        text-align: center;
        color: white;
        margin: 16px 24px 16px 0;
        float: left;
        line-height: 32px;
    }
</style>