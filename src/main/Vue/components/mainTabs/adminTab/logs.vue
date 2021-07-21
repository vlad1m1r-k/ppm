<template>
    <table class="table table-bordered table-striped table-sm">
        <thead>
        <tr>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('user')">User</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('act')">Act</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('object')">Object</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('objName')">ObjName</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('date')">Date</button>
            </th>
            <th>
                <button class="btn btn-link btn-sm" @click="setSort('comment')">Comment</button>
            </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="logRecord in logData.content" :key="'LD' + logRecord.id">
            <td>{{ logRecord.user }}</td>
            <td>{{ logRecord.act }}</td>
            <td>{{ logRecord.object }}</td>
            <td>{{ logRecord.objName }}</td>
            <td>{{ logRecord.dateStr }}</td>
            <td>{{ logRecord.comment }}</td>
        </tr>
        </tbody>
    </table>
    <div class="row justify-content-center">
        <div class="col-auto">
            <select v-model="pager.pageSize" @change="getLogs">
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50" selected>50</option>
                <option value="100">100</option>
            </select>
        </div>
        <div class="col-auto">
            <ul class="pagination pagination-sm">
                <li class="page-item text-dark" v-for="n in logData.totalPages" :class="{active: n - 1 === logData.number}"
                    :key="'LGPager' + n" style="cursor: pointer">
                    <a class="page-link" @click="setPage(n - 1)">{{ n }}</a>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import Pager from "../../../pager";

export default {
    name: "logs",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            pager: new Pager(50, "date", "desc"),
            logData: {}
        }
    },
    watch: {
        "pager.pageSize"(newSize, oldSize) {
            if (newSize !== oldSize) {
                this.pager.page = 0;
            }
        }
    },
    methods: {
        async getLogs() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({
                    token: token,
                    page: Number.parseInt(this.pager.page),
                    size: Number.parseInt(this.pager.pageSize),
                    direction: this.pager.direction,
                    field: this.pager.sortField
                });
                const answer = await $.ajax({
                    url: "/logs/getLogs",
                    method: "POST",
                    data: encryptedData
                });
                this.logData = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        setSort(field) {
            this.pager.setSort(field);
            this.getLogs();
        },
        setPage(page) {
            this.pager.page = page;
            this.getLogs();
        }
    },
    beforeMount() {
        this.getLogs();
    }
}
</script>

<style scoped>

</style>