package ds

import (
	"container/heap"
	"errors"
)

// all of this is copied from https://github.com/dominikbraun/graph/blob/main/collection.go

// PriorityQueue implements a minimum priority queue using a minimum binary heap
// that prioritizes smaller values over larger values.
type PriorityQueue[T comparable] struct {
	items *MinHeap[T]
	cache map[T]*PriorityItem[T]
}

// PriorityItem is an item on the binary heap consisting of a priority value and
// an actual payload value.
type PriorityItem[T comparable] struct {
	value    T
	priority float64
	index    int
}

func NewPriorityQueue[T comparable]() *PriorityQueue[T] {
	return &PriorityQueue[T]{
		items: &MinHeap[T]{},
		cache: map[T]*PriorityItem[T]{},
	}
}

// Len returns the total number of items in the priority queue.
func (p *PriorityQueue[T]) Len() int {
	return p.items.Len()
}

// Push pushes a new item with the given priority into the queue. This operation
// may cause a re-balance of the heap and thus scales with O(log n).
func (p *PriorityQueue[T]) Push(item T, priority float64) {
	if _, ok := p.cache[item]; ok {
		return
	}

	newItem := &PriorityItem[T]{
		value:    item,
		priority: priority,
		index:    0,
	}

	heap.Push(p.items, newItem)
	p.cache[item] = newItem
}

// Pop returns and removes the item with the lowest priority. This operation may
// cause a re-balance of the heap and thus scales with O(log n).
func (p *PriorityQueue[T]) Pop() (T, error) {
	if len(*p.items) == 0 {
		var empty T
		return empty, errors.New("priority queue is empty")
	}

	item := heap.Pop(p.items).(*PriorityItem[T])
	delete(p.cache, item.value)

	return item.value, nil
}

// UpdatePriority updates the priority of a given item and sets it to the given
// priority. If the item doesn't exist, nothing happens. This operation may
// cause a re-balance of the heap and this scales with O(log n).
func (p *PriorityQueue[T]) UpdatePriority(item T, priority float64) {
	targetItem, ok := p.cache[item]
	if !ok {
		return
	}

	targetItem.priority = priority
	heap.Fix(p.items, targetItem.index)
}

// MinHeap is a minimum binary heap that implements heap.Interface.
type MinHeap[T comparable] []*PriorityItem[T]

func (m *MinHeap[T]) Len() int {
	return len(*m)
}

func (m *MinHeap[T]) Less(i, j int) bool {
	return (*m)[i].priority < (*m)[j].priority
}

func (m *MinHeap[T]) Swap(i, j int) {
	(*m)[i], (*m)[j] = (*m)[j], (*m)[i]
	(*m)[i].index = i
	(*m)[j].index = j
}

func (m *MinHeap[T]) Push(item interface{}) {
	i := item.(*PriorityItem[T])
	i.index = len(*m)
	*m = append(*m, i)
}

func (m *MinHeap[T]) Pop() interface{} {
	old := *m
	item := old[len(old)-1]
	*m = old[:len(old)-1]

	return item
}
