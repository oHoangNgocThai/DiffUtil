# DiffUtil for update Recycle View

# Giới thiệu

* Việc sử dụng RecyclerView khi lập trình ứng dụng Android là việc rất thường xuyên của các lập trình viên. Việc đó cũng kéo theo nhu cầu cập nhật lại dữ liệu khi người dùng thao tác trên đó. Thông thường là lấy dữ liệu từ trên server và cập nhật dữ liệu mới nhận được vào danh sách đang sử dụng.
* Trong quá trình này, nếu gặp phải sự chậm trễ sẽ gây ra ảnh hưởng tới trải nghiệm người dùng, vì vậy sẽ phải tối ưu hóa việc cập nhật này một cách tốn ít tài nguyên nhất.
* Trước kia chúng ta thường sử dụng **notifyDataSetChanged()** để cập nhật những dữ liệu mới, nhưng việc đó gây ra việc load lại toàn bộ danh sách.
* Để giải quyết việc đó, Từ phiên bản RecycleView 24.2.0 trở lên đã cung cấp cho chúng ta class **DiffUtil**. Class này hỗ trợ việc tìm ra điểm khác nhau giữa hai danh sách và cải thiện việc cập nhật danh sách dưới dạng 1 đầu ra của class. Nó được sử dụng để thông báo cập nhật cho Adapter của RecycleView. Nó sử dụng thuật toán khác biệt của Eugene W.Myers để tinshra số lượng cập nhật tối thiểu để chuyển thành một danh sách khác.

# Sử dụng

## Thành phần
**DiffUtil.Callback** là một abstract class và sử dụng như một callback được gọi bởi DiffUtil khi tính toán sự khác nhau giữa hai danh sách. Nó có 4 phương thức abstract vào một phương thức không phải abstract. 

* **getOlsListSize()** - Trả về kích thước của danh sách cũ
* **getNewListSize()** - Trả về kích thước của danh sách mới
* **areItemsTheSame(int oldItemPosition, int newItemPosition)** - Nó quyết định xem hai đối tượng có trùng item với nhau không
* **areContentsTheSame(int oldItemPosition, int newItemPosition)** - Nó quyết định xem hai item có trùng dữ liệu của nhau không. Nó chỉ được gọi khi **areContentsTheSame()** trả về true.
* **getChangePayload(int oldItemPosition, int newItemPosition)** - Nếu areItemTheSame() trả về giá trị true và areContentsTheSame() trả về false, nghĩa là dữ liệu có sự thay đổi, DiffUtil sẽ gọi đến hàm này để xử lý việc thay dữ liệu cho item.

## Thực hiện

* Để thực hiện việc cập nhật dữ liệu cho adapter, ta cần tạo ra class DiffCallback để kiểm tra sự thay đổi giữa 2 danh sách cũ và mới

```
class ContactDiffCallback(
        private val mOldContactList: List<Contact>,
        private val mNewContactList: List<Contact>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return mOldContactList[oldPosition].id == mNewContactList[newPosition].id
    }

    override fun getOldListSize() = mOldContactList.size

    override fun getNewListSize() = mNewContactList.size

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val oldContact = mOldContactList[oldPosition]
        val newContact = mNewContactList[newPosition]
        return (oldContact.name == newContact.name && oldContact.age == newContact.age)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}
```

* Sau đó thực hiện hàm cập nhật ở trong adapter của RecyclerView như sau:

```
fun updateData(newList: List<Contact>) {
        val diffCallback = ContactDiffCalback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
```

> Nếu danh sách quá lớn, chúng ta nên đưa việc tính toán sự khác nhau vào trong background, sau đó lấy ra giá trị diffResult của DiffUtil sau đó mới cập nhật item của RecyclerView ở trong main thread. Tất nhiên là có giới hạn cho danh sách khoảng 2²⁶ phần tử.

## Hiệu năng

* DiffUtil yêu câù **O(N)** khoảng trống để tìm ra số lần thêm và xóa tối thiểu giữa hai danh sách. Hiệu suất dự kiến là **O(N + D²)** khi N là tổng số phần tử được thêm hoặc xóa và D là độ dài của tập lệnh chỉnh sửa. Để biết thêm về độ phức tạp và thời gian cụ thể của DiffUtil, xem [tại đây](https://developer.android.com/reference/android/support/v7/util/DiffUtil)

# Sử dụng trong background

## ListAdapter

* Là class được viết dựa trên DiffUtil, nếu sử dụng ListAdapter bạn sẽ không cần phải thực hiện hết tất cả hàm của DiffUtil mà vẫn nhận tự động các animation khi các item trong RecyclerView thay đổi.
* Được thêm vào thư viện support version 27.0.1 trở lên.
* Sử dụng thay thế cho Adapter của RecyclerView

```
class ContactListAdapter : ListAdapter<Contact, ContactListAdapter.ViewHolder>(ContactDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_contact, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Contact) {
            binding.contact = item
            binding.executePendingBindings()
        }
    }

    class ContactDiffCallBack : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldContactList: Contact, newContactList: Contact): Boolean {
            return oldContactList.id == newContactList.id
        }

        override fun areContentsTheSame(oldContactList: Contact, newContactList: Contact): Boolean {
            return oldContactList == newContactList
        }
    }
}
```

* Khi có nhu cầu cập nhật chỉ cần gọi đếm hàm **submitList()**, ListAdapter sẽ tự động gửi so sánh trong background và cập nhật trên RecycleView. Xem thêm [tại đây](https://developer.android.com/reference/android/support/v7/recyclerview/extensions/ListAdapter)

## AsyncListDiffer

* Là trình trợ giúp để tính toán sự khác nhau giữa 2 danh sách thông qua DiffUtil trên background thread.
* Nó có thể kết nối với **RecyclerView.Adapter** và sẽ báo cho asapter những thay đổi giữa hai danh sách được submit. 
* Thông thường để cho đơn giản thì ListAdapter thường được dùng thay vì dùng **AsyncListDiffer** một cách trực tiếp. AsyncListDiffer thường được sử dụng trong những trường hợp phức tạp hơn.
* Nó có thể sử dụng các giá trị trả về của LiveData và trình diễn dữ liệu dễ dàng cho adapter. Nó tính toán sự khác biệt trong nội dung danh sách thông qua DiffUtil trên background thread khi nhận được danh sách mới.

```
class ContactAsyncDifferAdapter : RecyclerView.Adapter<ContactAsyncDifferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_contact, parent, false))
    }

    override fun getItemCount() = mDiffer.currentList.size
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binData(mDiffer.currentList[position])
    }

    class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binData(item: Contact) {
            binding.contact = item
            binding.executePendingBindings()
        }
    }

    fun submitList(list: List<Contact>) {
        mDiffer.submitList(list)
    }

    private val mDiffer = AsyncListDiffer<Contact>(this, object : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldContact: Contact, newContact: Contact): Boolean {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldContact.id == newContact.id
        }

        override fun areContentsTheSame(oldContact: Contact, newContact: Contact): Boolean {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldContact.equals(newContact)
        }

    })
}

```

> Về cơ bản thì 2 loại trên giống nhau, tùy vào trường hợp mà sử dụng và chúng đều chạy trên background đối với những danh sách có nhiều phần tử.