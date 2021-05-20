<template>
    <dl>
        <dt class="border-bottom">
            <div class="row ml-0 mr-0">
                <div class="col">{{ item.name }}</div>
                <div class="col-auto pl-0">
                    <input type="checkbox" :checked="item.accessNA" @click.prevent="editAccess(item.accessNA, 'NA')">
                    NA
                </div>
                <div class="col-auto pl-0" :class="{'bg-info': item.accessPT}">
                    <input type="checkbox" :checked="item.accessPT" @click.prevent="editAccess(item.accessPT, 'PT')">
                    PT
                </div>
                <div class="col-auto pl-0" :class="{'bg-success': item.accessRO}">
                    <input type="checkbox" :checked="item.accessRO" @click.prevent="editAccess(item.accessRO, 'RO')">
                    RO
                </div>
                <div class="col-auto pl-0" :class="{'bg-danger': item.accessRW}">
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
dd {
    margin-left: 22px;
    margin-bottom: auto;
}
dl {
    margin-bottom: auto;
}
</style>