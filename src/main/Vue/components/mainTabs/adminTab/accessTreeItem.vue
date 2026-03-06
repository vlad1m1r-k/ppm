<template>
    <dl>
        <dt class="acc-view">
            <div class="acc-grid">
                <div>{{ item.name }}</div>
                <div>
                    <input type="checkbox" :checked="item.accessNA" @click.prevent="editAccess(item.accessNA, 'NA')">
                    NA
                </div>
                <div>
                    <input type="checkbox" :checked="item.accessPT" @click.prevent="editAccess(item.accessPT, 'PT')">
                    PT
                </div>
                <div>
                    <input type="checkbox" :checked="item.accessRO" @click.prevent="editAccess(item.accessRO, 'RO')">
                    RO
                </div>
                <div>
                    <input type="checkbox" :checked="item.accessRW" @click.prevent="editAccess(item.accessRW, 'RW')">
                    RW
                </div>
            </div>
        </dt>
        <dd v-for="child in item.children" :key="'ati' + child.id">
            <access-tree-item :item="child" :group="group" @update-tree="updateTree"></access-tree-item>
        </dd>
    </dl>
</template>

<script>
export default {
    name: "accessTreeItem",
    emits: ["update-tree"],
    props: {
        item: Object,
        group: Object
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language
        }
    },
    methods: {
        async editAccess(checked, access) {
            if (checked) {
                if (confirm(this.language.data.amg8 + this.group.name + "?")) {
                    this.eventHub.emit("show-msg", "");
                    try {
                        const token = await this.tokenProvider.getToken();
                        const encryptedData = await cryptoProvider.encrypt({
                            token: token,
                            containerId: this.item.id,
                            groupId: this.group.id,
                            access: access,
                        });
                        await $.ajax({
                            url: "/container/removeAccess",
                            method: "POST",
                            data: encryptedData
                        });
                        this.updateTree();
                    } catch (e) {
                        this.eventHub.emit("show-msg", this.errorParser(e));
                    }
                }
            } else {
                this.eventHub.emit("show-msg", "");
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        containerId: this.item.id,
                        groupId: this.group.id,
                        access: access,
                        ptAbove: false,
                        sameBelow: false
                    });
                    await $.ajax({
                        url: "/container/setAccess",
                        method: "POST",
                        data: encryptedData
                    });
                    this.updateTree();
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
        updateTree() {
            this.$emit("update-tree");
        }
    }
}
</script>

<style scoped>

</style>