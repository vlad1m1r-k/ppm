export default class {
    constructor(pageSize, sortField, direction) {
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.page = 0;
        this.direction = direction;
    }

    setSort(name) {
        if (this.sortField === name) {
            this.direction = this.direction === 'desc' ? 'asc' : 'desc';
        } else {
            this.sortField = name;
            this.direction = 'asc';
        }
    }
}