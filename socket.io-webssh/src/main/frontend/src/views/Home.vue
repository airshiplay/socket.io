<template>
    <div class="home">
        <device-list/>
    </div>
</template>

<script>

    import DeviceList from "./device/device-list";

    export default {
        name: 'home',
        components: {DeviceList},
    }
</script>
<style>

</style>
