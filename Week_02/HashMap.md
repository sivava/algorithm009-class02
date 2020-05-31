## 1.HashMap介绍
#### 1.1什么是HashMap
Hash table based implementation of the map interface.
#### 1.2HashMap特点
+ permits null values and null key
+ no guarantees as to the order of the map
+ todo 

## 2.HashMap属性
**常量：**
>+ DEFAULT_INITIAL_CAPACITY = 1 << 4
>+ MAXIMUM_CAPACITY = 1 << 30
>+ DEFAULT_LOAD_FACTOR = 0.75f
>+ TREEIFY_THRESHOLD = 8
>+ UNTREEIFY_THRESHOLD = 6
>+ MIN_TREEIFY_CAPACITY = 64

**成员属性：**
>+ Node<K,V>[] table
>+ Set<Map.Entry<K,V>> entrySet
>+ int size
>+ int modCount
>+ int threshold ： 再散列的阈值
>+ float loadFactor

## 3.内部类
#### 3.1 Node
hash, key, value,   
Node<key, value> next
#### 3.2 TreeNode
#### 3.3 Spliterator
## 4.构造方法
1. Constructs an empty HashMap with the specified initial capaticy and load factor

```
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
```
重点是最后一句：
```
    this.threshold = tableSizeFor(initialCapacity);
```
看一下这个方法都做了什么事情：
```
    /**
     * Returns a power of two size for the given target capacity.
     */
    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```
cap - 1 的意思是数组最后一位的下标。那么第一句话的意思就是-1 无符号右移，右移的位数为数组最后一位的下标转换为2进制表示，第一个1前面的0的个数。n就等于cap从第一个1开始，后面全是1，这个数再加1就是2的幂。  

**例：**
cap = 20， n = 31
序号 |步骤 | 值
:---:|---|---
1 | cap - 1 十进制 | 19
2 | cap - 1 二进制 | 00000000 00000000 00010011
3 | cap - 1 第一个1前面0的个数 | 19
4 | -1 源码(source code) | 10000000 00000000 00000001
5 | -1 反码(inverse code) | 11111111 11111111 11111110
6 | -1 补码(complement code) | 11111111 11111111 11111111
7 | -1的补码右移19位：31 | 00000000 00000000 00011111

所以最终的结果，也就是再散列的阈值，要么就是1，要么就是MAXIMUM_CAPACITY，而更多的是power of two。

2. Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75).

3. Constructs an empty HashMap with the default initial capacity (16) and the default load factor (0.75).

4. Constructs a new HashMap with the same mappings as the specified Map. The HashMap is created with default load factor (0.75) and an initial capacity sufficient to hold the mappings in the specified Map.

```
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
```
## 5.主要方法
#### 5.1 hash

```
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```
#### 5.2 put

```
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // 散列表为空时，调用resize方法初始化散列表
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // 未发生碰撞，直接插入
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            // 发生碰撞的情况
            Node<K,V> e; K k;
            // 如果key已经存在
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            // 红黑树
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                // 链表
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            // 桶中有重复key
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```

#### 5.3 get

```
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        // 根据hash值找到在数组中的位置。查看第一个entry的key是否一致，不一致则遍历list或者tree，没找到就返回null
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
```
#### 5.4 resize

#### 5.5 remove
#### 5.6 其他方法
compute  
merge  
readObject  
writeObject


## 0. TODO
1.如何验证HashMap进行了扩容？  
_是否可以通过查看jvm内存分布来确认_  
2.如何确认当前扩容阈值就是capacity * load factor
